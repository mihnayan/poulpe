package org.jtalks.common.security.acl;

import org.apache.commons.lang.IllegalClassException;
import org.jtalks.common.model.entity.Entity;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * @author stanislav bashkirtsev
 */
public class AclUtilTest {
    @Mock
    MutableAclService aclService;
    AclUtil util;

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        util = new AclUtil(aclService);
    }

    @Test(dataProvider = "randomSidsAndPermissions", dataProviderClass = AclDataProvider.class)
    public void testDeletePermissionFromAcl(List<Sid> sids, List<Permission> permissions) throws Exception {
        ExtendedMutableAcl acl = mock(ExtendedMutableAcl.class);
        List<AccessControlEntry> entries = createEntries(acl, sids, permissions);
        when(acl.getEntries()).thenReturn(entries);
        util.deletePermissionsFromAcl(acl, sids, permissions);
        verify(acl).delete(entries);
    }

    @Test
    public void testGetAclForObjectIdentity() throws Exception {
        ObjectIdentity identity = new ObjectIdentityImpl(getClass().getName(), 1);
        MutableAcl mockAcl = mock(MutableAcl.class);
        when(aclService.readAclById(identity)).thenReturn(mockAcl);

        ExtendedMutableAcl extendedMutableAcl = util.getAclFor(identity);
        assertSame(extendedMutableAcl.getAcl(), mockAcl);
    }

    @Test(dataProvider = "randomEntity", dataProviderClass = AclDataProvider.class)
    public void testCreateIdentityFor(Entity entity) throws Exception {
        ObjectIdentity identity = util.createIdentityFor(entity);
        assertEquals(identity.getType(), entity.getClass().getName());
        assertEquals(identity.getIdentifier(), entity.getId());
    }

    @Test(dataProvider = "notSavedEntity", dataProviderClass = AclDataProvider.class,
            expectedExceptions = IllegalArgumentException.class)
    public void testCreateIdentityFor_withWrongId(Entity entity) throws Exception {
        util.createIdentityFor(entity);
    }

    private List<AccessControlEntry> createEntries(MutableAcl acl, List<Sid> sids, List<Permission> permissions) {
        assertEquals(sids.size(), permissions.size(), "Provided lists should have the same size");
        List<AccessControlEntry> toReturn = new ArrayList<AccessControlEntry>(sids.size());
        for (int i = 0; i < sids.size(); i++) {
            toReturn.add(createEntry(acl, sids.get(i), permissions.get(i)));
        }
        return toReturn;
    }

    private AccessControlEntry createEntry(MutableAcl acl, Sid sid, Permission permission) {
        return new AccessControlEntryImpl(1L, acl, sid, permission, true, true, true);
    }
}