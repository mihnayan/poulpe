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
package org.jtalks.poulpe.web.controller.section;

import static org.jtalks.poulpe.web.controller.utils.ObjectsFactory.fakeBranch;
import static org.jtalks.poulpe.web.controller.utils.ObjectsFactory.fakeSection;
import static org.jtalks.poulpe.web.controller.utils.ObjectsFactory.fakeSections;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.jtalks.common.validation.EntityValidator;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.service.SectionService;
import org.jtalks.poulpe.web.controller.DialogManager;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Konstantin Akimov
 * @author Vahluev Vyacheslav
 * @author Alexey Grigorev
 */
public class SectionPresenterTest {

    private SectionPresenter presenter;

    @Mock SectionService service;
    @Mock ZkSectionView view;
    @Mock DialogManager dialogManager;
    @Mock EntityValidator entityValidator;
    @Mock ZkSectionTreeComponent currentSectionTreeComponent;
    @Mock PerfomableFactory perfomableFactory;
    @Mock DeleteSectionDialogPresenter dialogPresenter;
    
    
    private List<PoulpeSection> sections = fakeSections();
    private PoulpeSection section = sections.get(0);
    private PoulpeBranch branch = fakeBranch();
    
    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        presenter = new SectionPresenter();
        presenter.setSectionService(service);
        presenter.setDialogManager(dialogManager);
        presenter.setEntityValidator(entityValidator);
        presenter.setCurrentSectionTreeComponentImpl(currentSectionTreeComponent);
        presenter.setPerfomableFactory(perfomableFactory);
        presenter.setDeleteSectionDialogPresenter(dialogPresenter);
        
        when(service.getAll()).thenReturn(sections);
        presenter.initView(view);
    }
    
    @Test
    public void testInitView() {
        verify(view).addSections(sections);
    }

    @Test
    public void testOpenDeleteDialogOK() {
        presenter.openDeleteDialog(section);
        verify(dialogPresenter).show(section);
    }

    @Test
    public void testOpenDeleteDialogNullObject() {
        presenter.openDeleteDialog(null);
        verify(dialogPresenter, never()).show(section);
    }

    @Test
    public void testOpenDeleteDialogWithWrongArguments() {
        presenter.openDeleteDialog(branch);
        verify(perfomableFactory).deleteBranch(branch);
        verify(dialogManager).confirmDeletion(eq(branch.getName()), any(DialogManager.Performable.class));
    }

    @Test
    public void testOpenEditDialogNullParams() {
        givenNoElementSelected();
        
        presenter.openEditDialog(currentSectionTreeComponent);

        verify(view, never()).openEditBranchDialog(any(PoulpeBranch.class));
        verify(view, never()).openEditSectionDialog(null);
    }

    @Test
    public void testOpenEditSectionDialog() {
        givenSectionSelected();
        presenter.openEditDialog(currentSectionTreeComponent);
        verify(view).openEditSectionDialog(section);
    }

    @Test
    public void testOpenEditBranchDialog() {
        givenBranchSelected();
        presenter.openEditDialog(currentSectionTreeComponent);
        verify(view).openEditBranchDialog(branch);
    }

    private void givenBranchSelected() {
        when(currentSectionTreeComponent.getSelectedObject()).thenReturn(branch);
    }

    @Test
    public void testOpenNewSectionDialog() {
        presenter.openNewSectionDialog();
        verify(view).openNewSectionDialog();
    }

    @Test
    public void testOpenNewBranchDialog() {
        presenter.openNewBranchDialog(currentSectionTreeComponent);
        verify(view).openNewBranchDialog();
    }

    @Test
    public void testRemoveSectionFromView() {
        presenter.removeSectionFromView(section);
        verify(view).removeSection(section);
    }

    @Test
    public void testDeleteSectionNull() {
        givenNoElementSelected();
        presenter.deleteSection(null);
        verify(dialogManager, never()).confirmDeletion(anyString(), any(DialogManager.Performable.class));
        verify(dialogPresenter, never()).show(section);
    }

    private void givenNoElementSelected() { 
        // currentSectionTreeComponent.getSelectedObject() returns null
    }

    @Test
    public void testDeleteSectionNullWithName() {
        givenNoElementSelected();
        presenter.deleteSection(section);
        verify(dialogManager, never()).confirmDeletion(anyString(), any(DialogManager.Performable.class));
        verify(dialogPresenter, never()).show(section);
    }

    @Test
    public void testDeleteSectionOK() {
        givenSectionSelected();
        presenter.deleteSection(null);
        verify(dialogManager).confirmDeletion(anyString(), any(DialogManager.Performable.class));
        verify(perfomableFactory).deleteSection(section, null);
    }

    private void givenSectionSelected() {
        when(currentSectionTreeComponent.getSelectedObject()).thenReturn(section);
    }

    @Test
    public void testDeleteSectionOKWithName() {
        givenSectionSelected();
        PoulpeSection recipient = fakeSection();
        presenter.deleteSection(recipient);
        verify(dialogManager).confirmDeletion(eq(section.getName()), any(DialogManager.Performable.class));
        verify(perfomableFactory).deleteSection(section, recipient);
    }
    
    @Test 
    public void openModeratorDialog() {
        presenter.openModerationDialog(branch);
        verify(view).openModerationDialog(branch);
    }
    
    @Test 
    public void saveSection() {
        presenter.saveSection(section);
        verify(service).saveSection(section);
    }

}
