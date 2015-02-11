package cool.arch.maven.plugin.gitmerge.dao;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import cool.arch.maven.plugin.gitmerge.model.Release;
import cool.arch.maven.plugin.gitmerge.model.Releases;

/**
 * Stateful DAO to marshal the releases.json file to/from the filesystem.
 */
public final class ReleasesDao {

  private final ObjectMapper mapper = new ObjectMapper();

  private final File releasesJson;

  private Releases releases = null;

  /**
   * Creates a new ReleasesDao instance for a specific releases.json file.
   * @param releasesJson File for the releases.json file
   */
  public ReleasesDao(final File releasesJson) {
    requireNonNull(releasesJson, "releasesJson shall not be null");
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    reset();
    this.releasesJson = releasesJson;
  }

  /**
   * Answers whether there is an existing releases.json file.
   * @return Truth of whether the releases.json file exists
   */
  public boolean releasesJsonExists() {
    return releasesJson.exists();
  }

  /**
   * Reads the current state of the releases object from the filesystem.
   * @throws IOException If an error reading the file is detected
   */
  public void read() throws IOException {
    if (releases.getReleases() == null) {
      releases.setReleases(new ArrayList<Release>());
    }

    releases = mapper.readValue(releasesJson, Releases.class);
  }

  /**
   * Writes the current state of the releases object to the filesystem.
   * @throws IOException If an error writing the file is detected
   */
  public void write() throws IOException {
    mapper.writeValue(releasesJson, releases);
  }

  /**
   * Resets the Releases object to a default.
   */
  public void reset() {
    releases = new Releases();
    releases.setReleases(new ArrayList<Release>());
  }

  /**
   * Gets the releases releases.json File object.
   * @return File instance
   */
  public File getReleasesJson() {
    return releasesJson;
  }

  /**
   * Gets the Releases object.
   * @return Releases instance
   */
  public Releases getReleases() {
    return releases;
  }
}
