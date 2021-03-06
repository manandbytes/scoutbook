package org.eclipsescout.contacts.client.mobile.ui.forms;

import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ClientJob;
import org.eclipse.scout.rt.client.mobile.transformation.DeviceTransformationConfig;
import org.eclipse.scout.rt.client.mobile.transformation.DeviceTransformationUtility;
import org.eclipse.scout.rt.client.mobile.transformation.MobileDeviceTransformation;
import org.eclipse.scout.rt.client.mobile.ui.basic.table.AbstractMobileTable;
import org.eclipse.scout.rt.client.mobile.ui.desktop.MobileDesktopUtility;
import org.eclipse.scout.rt.client.mobile.ui.form.AbstractMobileForm;
import org.eclipse.scout.rt.client.mobile.ui.form.outline.IOutlineChooserForm;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.extension.client.ui.form.fields.button.AbstractExtensibleButton;
import org.eclipse.scout.rt.shared.AbstractIcons;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.contacts.client.mobile.ui.forms.MobileHomeForm.MainBox.LogoutButton;
import org.eclipsescout.contacts.client.mobile.ui.forms.MobileHomeForm.MainBox.OutlinesTableField;

public class MobileHomeForm extends AbstractMobileForm implements IOutlineChooserForm {

  public MobileHomeForm() throws ProcessingException {
    super();
  }

  @Override
  protected boolean getConfiguredAskIfNeedSave() {
    return false;
  }

  @Override
  protected int getConfiguredDisplayHint() {
    return DISPLAY_HINT_VIEW;
  }

  @Override
  protected String getConfiguredDisplayViewId() {
    return VIEW_ID_CENTER;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("MobileOutlineChooserTitle");
  }

  @Override
  protected boolean getConfiguredFooterVisible() {
    return true;
  }

  public LogoutButton getLogoutButton() {
    return getFieldByClass(LogoutButton.class);
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public OutlinesTableField getOutlinesTableField() {
    return getFieldByClass(OutlinesTableField.class);
  }

  @Order(10.0)
  public class MainBox extends AbstractGroupBox {

    @Override
    protected boolean getConfiguredBorderVisible() {
      return false;
    }

    @Override
    protected void execInitField() throws ProcessingException {
      // Table already is scrollable, it's not necessary to make the form scrollable too
      DeviceTransformationConfig config = DeviceTransformationUtility.getDeviceTransformationConfig();
      if (config != null) {
        config.excludeFieldTransformation(this, MobileDeviceTransformation.MAKE_MAINBOX_SCROLLABLE);
      }
    }

    @Order(10.0)
    public class OutlinesTableField extends AbstractTableField<OutlinesTableField.Table> {

      @Order(10.0)
      public class Table extends AbstractMobileTable {

        @Override
        protected boolean execIsAutoCreateTableRowForm() {
          return false;
        }

        @Override
        protected boolean getConfiguredAutoDiscardOnDelete() {
          return true;
        }

        @Override
        protected String getConfiguredDefaultIconId() {
          return AbstractIcons.TreeNode;
        }

        @Override
        protected boolean getConfiguredAutoResizeColumns() {
          return true;
        }

        @Override
        protected boolean getConfiguredSortEnabled() {
          return false;
        }

        @Override
        protected void execDecorateRow(ITableRow row) throws ProcessingException {
          String outlineIcon = getOutlineColumn().getValue(row).getIconId();
          if (outlineIcon != null) {
            row.setIconId(outlineIcon);
          }
        }

        @Override
        protected void execRowsSelected(ITableRow[] rows) throws ProcessingException {
          if (rows == null || rows.length == 0) {
            return;
          }
          IOutline outline = getOutlineColumn().getValue(rows[0]);
          MobileDesktopUtility.activateOutline(outline);
          getDesktop().removeForm(MobileHomeForm.this);
          clearSelectionDelayed();
        }

        public LabelColumn getLabelColumn() {
          return getColumnSet().getColumnByClass(LabelColumn.class);
        }

        public OutlineColumn getOutlineColumn() {
          return getColumnSet().getColumnByClass(OutlineColumn.class);
        }

        @Order(10.0)
        public class OutlineColumn extends AbstractColumn<IOutline> {

          @Override
          protected boolean getConfiguredDisplayable() {
            return false;
          }
        }

        @Order(20.0)
        public class LabelColumn extends AbstractStringColumn {
        }
      }

      @Override
      protected boolean getConfiguredLabelVisible() {
        return false;
      }

      @Override
      protected int getConfiguredGridH() {
        return 2;
      }
    }

    @Order(20.0)
    public class LogoutButton extends AbstractExtensibleButton {

      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("Logoff");
      }

      @Override
      protected void execClickAction() throws ProcessingException {
        ClientJob.getCurrentSession().stopSession();
      }
    }

  }

  public class ViewHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() throws ProcessingException {
      OutlinesTableField.Table table = getOutlinesTableField().getTable();
      IOutline[] outlines = getDesktop().getAvailableOutlines();

      for (IOutline outline : outlines) {
        if (outline.isVisible() && outline.getRootNode() != null) {
          ITableRow row = table.createRow(new Object[]{outline, outline.getTitle()});
          row.setEnabled(outline.isEnabled());
          table.addRow(row);
        }
      }
    }

    @Override
    protected void execFinally() throws ProcessingException {
      OutlinesTableField.Table table = getOutlinesTableField().getTable();
      table.discardAllRows();
    }
  }

  public void startView() throws ProcessingException {
    startInternal(new ViewHandler());
  }
}
