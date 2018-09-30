package sam.anime.entities;

import static sam.anime.meta.AnimeDirsMeta.LAST_MODIFIED;
import static sam.anime.meta.AnimeDirsMeta.PATH;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class AnimeDir {
	
		private final String path;
		private final long last_modified;
		
		public String getPath() { return path; }
		public long getLastModified() { return last_modified; }
		
		public AnimeDir(ResultSet rs) throws SQLException {
			this.path = rs.getString(PATH);
			this.last_modified = rs.getLong(LAST_MODIFIED);
		}
		
		public AnimeDir(String path, long last_modified) {
			this.path = Objects.requireNonNull(path);
			this.last_modified = last_modified;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AnimeDir other = (AnimeDir) obj;
			if (path == null) {
				if (other.path != null)
					return false;
			} else if (!path.equals(other.path))
				return false;
			return true;
		}
		
}
