import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Directories {

	public static class File {

		private String name;
		private int size;

		public File(String name, int size) {
			this.name = name;
			this.size = size;
		}

		public int getSize() {
			return size;
		}
	}

	private static class Directory {
		
		private TreeMap<String, Directory> childrenDirectories = new TreeMap<String, Directory>();
		private Directory parent = null;
		private String name;
		private List<File> childrenFiles = new ArrayList<File>();
		
		public Directory(Directory parent, String name) {
			this.name = name;
			this.parent = parent;
		}

		public Directory getParen() {
			return parent;
		}

		public Directory getChildrenDirectory(String name) {
			return childrenDirectories.get(name);
		}

		public void appendDirectory(String name) {
			childrenDirectories.put(name, new Directory(this, name));
		}

		public void appendFile(File file) {
			childrenFiles.add(file);
		}

		public void collectSmall(List<Directory> smallDirectories) {
			if (getSize()<=100000) {
				smallDirectories.add(this);
			}
			childrenDirectories.values().forEach(c -> c.collectSmall(smallDirectories));
		}

		private int getSize() {
			int size = childrenFiles.stream().mapToInt(f -> f.getSize()).sum();
			size += childrenDirectories.values().stream().mapToInt(d -> d.getSize()).sum();
			return size;
		}

		public Directory findDirectoryToDelete(Directory smallestDirectory, int spaceToDelete) {
			Directory result = smallestDirectory;
			if (getSize() >= spaceToDelete && (smallestDirectory == null || getSize() < smallestDirectory.getSize())) {
				result = this;
			}
			for (Directory directory : childrenDirectories.values()) {
				result = directory.findDirectoryToDelete(result, spaceToDelete);
			}
			return result;
		}
	}
	
	public static class Context {

		private Directory ROOT = new Directory(null, "root");
		
		private Directory currentDirectory = null;

		public Context() {
		}

		public void process(String line) {
			if (line.startsWith("$")) {
				processCommand(line.substring(1).trim());
			}
			else {
				processCommandResult(line);
			}
		}

		private void processCommandResult(String line) {
			String[] split = line.split(" ");
			if (split[0].equals("dir")) {
				currentDirectory.appendDirectory(split[1]);
			}
			else {
				currentDirectory.appendFile(new File(split[1], Integer.parseInt(split[0])));
			}
		}

		private void processCommand(String command) {
			if (command.startsWith("cd")) {
				String argument = command.split(" ")[1];
				if (argument.equals("/")) {
					currentDirectory = ROOT;
				}
				else if (argument.equals("..")) {
					currentDirectory = currentDirectory.getParen();
				}
				else {
					currentDirectory = currentDirectory.getChildrenDirectory(argument);
				}
			}
			else if (command.startsWith("ls")) {
			}
		}

		public Directory getRootDirectory() {
			return ROOT;
		}
	}
	
	public static void main(String[] args) throws IOException {
			Stream<String> lines = Files.lines(Paths.get("./input_directories.txt"));
			List<String> collect = lines.collect(Collectors.toList());
			
			Context context = new Context();
			for(String line : collect) {
				context.process(line.trim());
			}
			
			List<Directory> smallDirectories = new ArrayList<Directory>();
			context.getRootDirectory().collectSmall(smallDirectories);
			
			int result = smallDirectories.stream().mapToInt(d -> d.getSize()).sum();
			System.out.println(result);
			
			int totalDiskSpace = 70000000;
			int requiredSpace = 30000000;
			int usedSpace = context.getRootDirectory().getSize();
			int spaceToDelete = Math.max(0, requiredSpace - (70000000 - usedSpace));
			
			Directory directoryToDelete = context.getRootDirectory().findDirectoryToDelete(null, spaceToDelete);
			System.out.println(directoryToDelete.getSize());
	}
}

