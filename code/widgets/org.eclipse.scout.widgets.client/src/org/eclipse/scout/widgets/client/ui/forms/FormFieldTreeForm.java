/*******************************************************************************
 * Copyright (c) 2013 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.widgets.client.ui.forms;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.svg.client.SVGUtility;
import org.eclipse.scout.svg.client.svgfield.AbstractSvgField;
import org.eclipse.scout.svg.client.svgfield.SvgFieldEvent;
import org.eclipse.scout.widgets.client.Activator;
import org.eclipse.scout.widgets.client.ui.forms.FormFieldTreeForm.MainBox.FormFieldTreeField;
import org.w3c.dom.svg.SVGDocument;

public class FormFieldTreeForm extends AbstractForm {
  IPage m_page;

  public FormFieldTreeForm(IPage page) throws ProcessingException {
    super();
    m_page = page;
  }

  @Override
  protected int getConfiguredDisplayHint() {
    return DISPLAY_HINT_VIEW;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("FormFieldTree");
  }

  public void startPageForm() throws ProcessingException {
    startInternal(new PageFormHandler());
  }

  public FormFieldTreeField getFormFieldTreeField() {
    return getFieldByClass(FormFieldTreeField.class);
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  @Order(10.0)
  public class MainBox extends AbstractGroupBox {

    @Order(10.0)
    public class FormFieldTreeField extends AbstractSvgField {
      private SVGDocument m_formfieldtree;
      private SVGDocument m_valuefieldtree;
      private SVGDocument m_compositetree;

      @Override
      protected void execInitField() throws ProcessingException {
        try {
          URL url = Activator.getDefault().getBundle().getResource("/resources/svg/formfieldtree.svg");
          InputStream is = url.openStream();
          m_formfieldtree = SVGUtility.readSVGDocument(is);

          url = Activator.getDefault().getBundle().getResource("/resources/svg/valuefieldtree.svg");
          is = url.openStream();
          m_valuefieldtree = SVGUtility.readSVGDocument(is);

          url = Activator.getDefault().getBundle().getResource("/resources/svg/compositefieldtree.svg");
          is = url.openStream();
          m_compositetree = SVGUtility.readSVGDocument(is);

          setSvgDocument(m_formfieldtree);
        }
        catch (IOException e) {
          e.printStackTrace();
          throw new ProcessingException("Exception occured while reading svg file", e);
        }
      }

      @Override
      protected String getConfiguredBackgroundColor() {
        return "E3F4FF";
      }

      @Override
      protected int getConfiguredGridH() {
        return 10;
      }

      @Override
      protected int getConfiguredGridW() {
        return 0;
      }

      @Override
      protected boolean getConfiguredLabelVisible() {
        return false;
      }

      @Override
      protected void execHyperlink(SvgFieldEvent e) throws ProcessingException {
        String url = e.getURL().toExternalForm();

        String url_form = url.split("/")[url.split("/").length - 1];

        for (ITreeNode node : m_page.getChildNodes()) {
          for (String fieldName : node.getCellForUpdate().getText().split(" ")) {
            if (fieldName.toLowerCase().startsWith(url_form)) {
              m_page.getTree().selectNode(node);
              return;
            }
          }
        }
        if (url_form.equals("back")) {
          setSvgDocument(m_formfieldtree);
        }
        else if (url_form.equals("valuefield")) {
          setSvgDocument(m_valuefieldtree);
        }
        else if (url_form.equals("compositefield")) {
          setSvgDocument(m_compositetree);
        }
      }
    }
  }

  public class PageFormHandler extends AbstractFormHandler {
  }
}
