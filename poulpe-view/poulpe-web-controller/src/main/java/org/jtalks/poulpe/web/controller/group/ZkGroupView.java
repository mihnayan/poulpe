/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.web.controller.group;

import java.util.List;

import org.jtalks.poulpe.model.entity.PoulpeGroup;
import org.jtalks.poulpe.web.controller.ZkHelper;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * @author Konstantin Akimov
 * @author Vyacheslav Zhivaev
 */
@SuppressWarnings("serial")
public class ZkGroupView extends Window implements AfterCompose {

    private GroupPresenter presenter;

    private ZkHelper zkHelper = new ZkHelper(this);

    private Window editDialog;

    private Listbox groupsListbox;
    private ListModelList<PoulpeGroup> groupsListboxModel;
    private Textbox searchTextbox;

    @Override
    public void afterCompose() {
        zkHelper.wireByConvention();

        groupsListbox.setItemRenderer(new ListitemRenderer<PoulpeGroup>() {
            @Override
            public void render(Listitem listItem, PoulpeGroup group, int index) throws Exception {
                new Listcell(group.getName()).setParent(listItem);
                new Listcell("Not specified yet").setParent(listItem);
                listItem.setId(String.valueOf(group.getId()));
            }
        });

        groupsListbox.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                enableRemoveAndEditButtons();
            }

        });

        presenter.initView(this);
    }

    public void updateView(List<PoulpeGroup> groups) {
        groupsListboxModel = new ListModelList<PoulpeGroup>(groups);
        groupsListbox.setModel(groupsListboxModel);
        disableRemoveAndEditButtons();
    }

    public void onDoubleClick$groupsListbox() {
        presenter.onEditGroup(getSelectedGroup());
    }

    public void onClick$addButton() {
        presenter.onAddGroup();
    }

    public void onClick$removeButton() {
        presenter.deleteGroup(getSelectedGroup());
    }

    public void onClick$editMembersButton() {
        presenter.editMembers(getSelectedGroup());
    }

    public void onSearchAction() {
        presenter.doSearch(searchTextbox.getText());
    }

    public void openNewDialog() {
        EditGroupDialogView component = getEditView();
        component.show();
    }

    private EditGroupDialogView getEditView() {
        ZkEditGroupDialogView component = getEditDialogComponent();
        component.setAttribute("presenter", presenter);
        component.setAttribute("backWindow", this);
        return component;
    }

    private ZkEditGroupDialogView getEditDialogComponent() {
        return (ZkEditGroupDialogView) getDesktop().getPage("GroupDialog").getFellow("editWindow");
    }

    public void openEditDialog(PoulpeGroup group) {
        EditGroupDialogView component = getEditView();
        component.show(group);
    }

    public void onHideDialog() {
        presenter.updateView();
    }

    public PoulpeGroup getSelectedGroup() {
        return groupsListboxModel.getElementAt(groupsListbox.getSelectedIndex());
    }

    private void enableRemoveAndEditButtons() {
        ((Button) getFellow("removeButton")).setDisabled(false);
        ((Button) getFellow("editMembersButton")).setDisabled(false);
    }

    private void disableRemoveAndEditButtons() {
        ((Button) getFellow("removeButton")).setDisabled(true);
        ((Button) getFellow("editMembersButton")).setDisabled(true);
    }

    public void setPresenter(GroupPresenter presenter) {
        this.presenter = presenter;
    }

}