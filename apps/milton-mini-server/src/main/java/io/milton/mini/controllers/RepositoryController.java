package io.milton.mini.controllers;

import io.milton.annotations.ChildrenOf;
import io.milton.annotations.Get;
import io.milton.annotations.MakeCollection;
import io.milton.annotations.Principal;
import io.milton.annotations.PutChild;
import io.milton.annotations.ResourceController;
import io.milton.cloud.common.CurrentDateService;
import io.milton.http.Range;
import io.milton.http.Request;
import io.milton.vfs.data.DataSession;
import io.milton.vfs.db.Branch;
import io.milton.vfs.db.Profile;
import io.milton.vfs.db.Repository;
import io.milton.vfs.db.utils.SessionManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.inject.Inject;
import org.hashsplit4j.api.BlobStore;
import org.hashsplit4j.api.HashStore;
import org.hibernate.Session;

/**
 *
 * @author brad
 */
@ResourceController
public class RepositoryController {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RepositoryController.class);
    @Inject
    private HashStore hashStore;
    @Inject
    private BlobStore blobStore;
    @Inject
    private CurrentDateService currentDateService;

    public DataSession dataSession(Request request, Repository repo) {
        return dataSession(request, repo, false, null);
    }
    
    public DataSession dataSession(Request request, Repository repo, boolean autoCreateBranch, Profile currentUser) {
        String sessKey = "dataSession-" + repo.getId();
        DataSession dataSession = (DataSession) request.getAttributes().get(sessKey);
        if (dataSession == null) {
            Session session = SessionManager.session();
            Branch trunk = repo.liveBranch();
            if( trunk == null ) {
                if( autoCreateBranch ) {
                    trunk = repo.createBranch(Branch.TRUNK, currentUser, session);
                }                
            }
            if (trunk != null) {
                dataSession = new DataSession(trunk, session, hashStore, blobStore, currentDateService);
                request.getAttributes().put(sessKey, dataSession);
            }
        }
        return dataSession;
    }

    @ChildrenOf
    public List<DataSession.DataNode> getBranchMembers(Repository repo, Request request) {
        DataSession dataSession = dataSession(request, repo);
        if (dataSession != null) {
            return dataSession.getRootDataNode().getChildren();
        } else {
            return null;
        }
    }

    @ChildrenOf
    public List<DataSession.DataNode> getFolderMembers(DataSession.DirectoryNode dir) {
        return dir.getChildren();
    }

    @MakeCollection
    public DataSession.DirectoryNode createCollection(Repository repo, String name, Request request, @Principal Profile user) throws IOException {
        DataSession dataSession = dataSession(request, repo, true, user);
        return createCollection(dataSession.getRootDataNode(), name, repo, request, user);
    }

    @MakeCollection
    public DataSession.DirectoryNode createCollection(DataSession.DirectoryNode dir, String name, Repository repo, Request request, @Principal Profile user) throws IOException {
        DataSession.DirectoryNode newDir = dir.addDirectory(name);
        dataSession(request, repo).save(user);
        return newDir;
    }

    @Get
    public void getFileContent(DataSession.FileNode fileNode, OutputStream out, Range range) throws IOException {
        if (range == null) {
            fileNode.writeContent(out);
        } else {
            fileNode.writeContent(out, range.getStart(), range.getFinish());
        }
    }

    @PutChild
    public DataSession.FileNode createFile(Repository repo, String newName, InputStream inputStream, Request request, @Principal Profile user) throws IOException {
        DataSession dataSession = dataSession(request, repo, true, user);
        return createFile(dataSession.getRootDataNode(), newName, inputStream, request, repo, user);
    }
    
    
    @PutChild
    public DataSession.FileNode createFile(DataSession.DirectoryNode parent, String newName, InputStream inputStream, Request request, Repository repo, @Principal Profile principal) throws IOException {
        log.trace("createNew: set content");
        DataSession.FileNode newFileNode = parent.addFile(newName);
        newFileNode.setContent(inputStream);
        dataSession(request, repo).save(principal);
        return newFileNode;
    }
    
    @PutChild
    public DataSession.FileNode updateFile(DataSession.FileNode file, InputStream in) throws IOException {
        file.setContent(in);
        return file;
    }
}
