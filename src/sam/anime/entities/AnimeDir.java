package sam.anime.entities;


import java.sql.ResultSet;
import java.sql.SQLException;

import sam.anime.meta.AnimeDirsMeta;

public class AnimeDir {
	 private final int mal_id;
	    private final int id;
	    private final int parent_id;
	    private final String subpath;
	    private final int last_modified;

		public AnimeDir(ResultSet rs) throws SQLException {
	        this.mal_id = rs.getInt(AnimeDirsMeta.MAL_ID);
	        this.id = rs.getInt(AnimeDirsMeta.ID);
	        this.parent_id = rs.getInt(AnimeDirsMeta.PARENT_ID);
	        this.subpath = rs.getString(AnimeDirsMeta.SUBPATH);
	        this.last_modified = rs.getInt(AnimeDirsMeta.LAST_MODIFIED);
	    }
	    
	    public int getMalId(){ return this.mal_id; }
	    public int getId(){ return this.id; }
	    public int getParentId(){ return this.parent_id; }
	    public String getSubpath(){ return this.subpath; }
	    public int getLastModified(){ return this.last_modified; }
	    
		@Override
		public int hashCode() {
			return id;
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
			if(id < 0) 
				return subpath.equals(other.subpath);
			else
				return id == other.id;
		}
		
}
