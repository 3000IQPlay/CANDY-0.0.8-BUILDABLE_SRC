package fc.client.candy.manager;

import fc.client.candy.OyVey;
import fc.client.candy.features.Feature;
import fc.client.candy.features.modules.Module;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager extends Feature {
  private final Path base = getMkDirectory(getRoot(), new String[] { "candy" });
  
  private final Path config = getMkDirectory(this.base, new String[] { "config" });
  
  public FileManager() {
    getMkDirectory(this.base, new String[] { "pvp" });
    for (Module.Category category : OyVey.moduleManager.getCategories()) {
      getMkDirectory(this.config, new String[] { category.getName() });
    } 
  }
  
  public static boolean appendTextFile(String data, String file) {
    try {
      Path path = Paths.get(file, new String[0]);
      Files.write(path, Collections.singletonList(data), StandardCharsets.UTF_8, new OpenOption[] { Files.exists(path, new java.nio.file.LinkOption[0]) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE });
    } catch (IOException e) {
      System.out.println("WARNING: Unable to write file: " + file);
      return false;
    } 
    return true;
  }
  
  public static List<String> readTextFileAllLines(String file) {
    try {
      Path path = Paths.get(file, new String[0]);
      return Files.readAllLines(path, StandardCharsets.UTF_8);
    } catch (IOException e) {
      System.out.println("WARNING: Unable to read file, creating new file: " + file);
      appendTextFile("", file);
      return Collections.emptyList();
    } 
  }
  
  private String[] expandPath(String fullPath) {
    return fullPath.split(":?\\\\\\\\|\\/");
  }
  
  private Stream<String> expandPaths(String... paths) {
    return Arrays.<String>stream(paths).map(this::expandPath).flatMap(Arrays::stream);
  }
  
  private Path lookupPath(Path root, String... paths) {
    return Paths.get(root.toString(), paths);
  }
  
  private Path getRoot() {
    return Paths.get("", new String[0]);
  }
  
  private void createDirectory(Path dir) {
    try {
      if (!Files.isDirectory(dir, new java.nio.file.LinkOption[0])) {
        if (Files.exists(dir, new java.nio.file.LinkOption[0]))
          Files.delete(dir); 
        Files.createDirectories(dir, (FileAttribute<?>[])new FileAttribute[0]);
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  private Path getMkDirectory(Path parent, String... paths) {
    if (paths.length < 1)
      return parent; 
    Path dir = lookupPath(parent, paths);
    createDirectory(dir);
    return dir;
  }
  
  public Path getBasePath() {
    return this.base;
  }
  
  public Path getBaseResolve(String... paths) {
    String[] names = expandPaths(paths).<String>toArray(x$0 -> new String[x$0]);
    if (names.length < 1)
      throw new IllegalArgumentException("missing path"); 
    return lookupPath(getBasePath(), names);
  }
  
  public Path getMkBaseResolve(String... paths) {
    Path path = getBaseResolve(paths);
    createDirectory(path.getParent());
    return path;
  }
  
  public Path getConfig() {
    return getBasePath().resolve("config");
  }
  
  public Path getCache() {
    return getBasePath().resolve("cache");
  }
  
  public Path getMkBaseDirectory(String... names) {
    return getMkDirectory(getBasePath(), new String[] { expandPaths(names).collect(Collectors.joining(File.separator)) });
  }
  
  public Path getMkConfigDirectory(String... names) {
    return getMkDirectory(getConfig(), new String[] { expandPaths(names).collect(Collectors.joining(File.separator)) });
  }
}
