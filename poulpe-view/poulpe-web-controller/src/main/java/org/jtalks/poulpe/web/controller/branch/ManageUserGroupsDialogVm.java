package org.jtalks.poulpe.web.controller.branch;

import org.jtalks.poulpe.model.entity.Group;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.ListModelList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Feeds the dialog for adding/removing groups for permissions. The page has 2 lists: available groups & those that are
 * already granted/restricted to the permission. So when the user selects some items from one list and moves them to
 * another, a command is triggered in {@link BranchPermissionManagementVm} which then delegates the actual changing of
 * the lists to this view model.
 *
 * @author stanislav bashkirtsev
 * @see BranchPermissionManagementVm
 */
@NotThreadSafe
public class ManageUserGroupsDialogVm {
    @SuppressWarnings("unchecked")
    private final ListModelList<Group> availableListModel = new BindingListModelList(new ArrayList(), false);
    @SuppressWarnings("unchecked")
    private final ListModelList<Group> addedListModel = new BindingListModelList(new ArrayList(), false);
    private final Set<Group> movedToAdded = new HashSet<Group>();
    private final Set<Group> movedFromAdded = new HashSet<Group>();


    /**
     * Searches for the list items that were selected in the list of available groups and removes them from that list;
     * then it adds those items to the list of already added groups.
     *
     * @return the groups that were moved from list of available groups to the list of those that were added
     */
    public Set<Group> moveSelectedToAddedGroups() {
        Set<Group> selection = new HashSet<Group>(availableListModel.getSelection());
        for (Group nextSelectedItem : selection) {
            addedListModel.add(nextSelectedItem);
            availableListModel.remove(nextSelectedItem);
        }
        return selection;
    }

    /**
     * Searches for the list items that were selected in the list of added groups and removes them from that list; then
     * it adds those items to the list of available groups.
     *
     * @return those selected groups that were moved
     */
    public Set<Group> moveSelectedFromAddedGroups() {
        Set<Group> selection = new HashSet<Group>(addedListModel.getSelection());
        for (Group nextSelectedItem : selection) {
            availableListModel.add(nextSelectedItem);
            addedListModel.remove(nextSelectedItem);
        }
        return selection;
    }

    /**
     * Moves all the list items from the list of available groups to the list of added.
     *
     * @return the groups that were moved
     */
    public Set<Group> moveAllToAddedGroups() {
        Set<Group> innerAvailableList = new HashSet<Group>(availableListModel.getInnerList());
        for (Group nextSelectedItem : innerAvailableList) {
            addedListModel.add(nextSelectedItem);
            availableListModel.remove(nextSelectedItem);
        }
        return innerAvailableList;
    }

    /**
     * Moves all the list items from the list of added to the list of available groups.
     *
     * @return the groups that were moved
     */
    public Set<Group> moveAllFromAddedGroups() {
        Set<Group> innerAddedList = new HashSet<Group>(addedListModel.getInnerList());
        for (Group nextSelectedItem : innerAddedList) {
            availableListModel.add(nextSelectedItem);
            addedListModel.remove(nextSelectedItem);
        }
        return innerAddedList;
    }

    /**
     * Clears the previous content of the list of available groups and fills it with the new groups.
     *
     * @param availableGroups new group list to feed the list box of groups that are available for adding
     * @return this
     */
    public ManageUserGroupsDialogVm setAvailableGroups(@Nonnull List<Group> availableGroups) {
        this.availableListModel.clear();
        this.availableListModel.addAll(availableGroups);
        return this;
    }

    /**
     * Clears the previous content of the list those groups that already granted to the permission and fills it with the
     * new groups.
     *
     * @param addedGroups new group list to feed the list box of groups that are already granted to the permission
     * @return this
     */
    public ManageUserGroupsDialogVm setAddedGroups(@Nonnull List<Group> addedGroups) {
        this.addedListModel.clear();
        this.addedListModel.addAll(addedGroups);
        return this;
    }

    /**
     * Feeds the list box of groups that are available to be granted to the permission.
     *
     * @return groups that are available to be granted to the permission
     */
    public ListModelList<Group> getAvailableGroups() {
        return availableListModel;
    }

    /**
     * Feeds the list box of groups that are already granted to the permission.
     *
     * @return groups that are available to be granted to the permission
     */
    public ListModelList<Group> getAddedGroups() {
        return addedListModel;
    }

}