import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

public class H2Cleaner {
	
	private static final String H2_PATH = "D:\\DATA\\Clients\\Libourne\\20200720_BackupBDD\\repository5.mv.db";

	public static void main(final String[] args) {
	    // open the store (in-memory if fileName is null)
	    final MVStore store = MVStore.open(H2_PATH);

	    final MVMap<Object, Object> openMap = store.openMap("undoLog");

	    openMap.clear();

	    // close the store (this will persist changes)
	    store.close();
	}
	
}
