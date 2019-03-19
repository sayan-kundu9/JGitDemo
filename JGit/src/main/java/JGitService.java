import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;

public class JGitService {

	public static void main(String[] args) {

		String URL = "https://git.sami.int.thomsonreuters.com//Sayan.Kundu//TestGit";
		String username = "sayan.kundu";
		String pass = "Welcome1";
		
		try {
			File localPath = new File("C://Users//UX015793//Downloads//TestGitRepository");
			localPath.mkdirs();
			
			
			System.out.println("local repo created : "+ createGitRepository(localPath));
			
			System.out.println("added to git repo : "+ addToGitRepository("C://Users//UX015793//Downloads//TestGitRepository", username, pass));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static boolean createGitRepository(File localPath) {

		try {
			//Files.delete(localPath.toPath());
			Git git = Git.init().setDirectory(localPath).call();
			
			StoredConfig config = git.getRepository().getConfig();
			config.setString("remote", "origin", "url", "https://git.sami.int.thomsonreuters.com//Sayan.Kundu//TestGit.git");
			config.save();
			
			

			System.out.println("Created repository: " + git.getRepository().getDirectory());
			File myFile = new File(git.getRepository().getDirectory().getParent(), "sample.txt");
			if (!myFile.createNewFile()) {
				throw new IOException("Could not create file " + myFile);
			}

			// run the add-call git.add().addFilepattern("enhanceCPS.txt").call();

			git.commit().setMessage("Initial commit").call();
			System.out.println("Committed file " + myFile + " to repository at " + git.getRepository().getDirectory());

			// Create a few branches for testing
			git.checkout().setCreateBranch(true).setName("branch2").call();

			// List all branches
			List<Ref> call = git.branchList().call();
			for (Ref ref : call) {
				System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
			}

			// Create a few new file
			File f = new File(git.getRepository().getDirectory().getParent(), "sample.txt");
			f.createNewFile();
			git.add().addFilepattern("sample.txt").call();

			// committed file
			Status status = git.status().call();
			Set<String> added = status.getAdded();
			for (String add : added) {
				System.out.println("Added: " + add);
			}
			// uncommitted files
			Set<String> uncommittedChanges = status.getUncommittedChanges();
			for (String uncommitted : uncommittedChanges) {
				System.out.println("Uncommitted: " + uncommitted);
			}

			// untracked file
			Set<String> untracked = status.getUntracked();
			for (String untrack : untracked) {
				System.out.println("Untracked: " + untrack);
			}

			// Find the head for the repository 
			ObjectId lastCommitId =	git.getRepository().resolve(Constants.HEAD);
			System.out.println("Head points to the following commit :" + lastCommitId.getName());
			
			
			git.close();
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;

	}
	
	public static boolean addToGitRepository(String localPath, String USERNAME, String PASSWORD) {
		try {

			Git git = Git.open(new File(localPath));

			AddCommand add = git.add();
			try {
				add.addFilepattern(".").call();
				CommitCommand commit = git.commit();
				commit.setMessage("latest commit").call();
				try {
					
					System.out.println("done !!");
					/*PushCommand pushCommand = git.push();
					pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD));
					pushCommand.call();*/

					Iterable<RevCommit> log = git.log().call();
					for (RevCommit revCommit : log) {
						System.out.println(revCommit.getCommitTime() + " " + revCommit.getFullMessage() + "");
					}
				} catch (GitAPIException e) {
					throw new RuntimeException(e);
				}
			} catch (NoFilepatternException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
