import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by stephenwebel1 on 4/28/16.
 */
public class Position {
    public static final String[] ENTITIES = new String[]{"US", "UK", "JP", "IN"};
    protected String _entity;
    protected int _qty;

    public Position() {
        _entity = ENTITIES[ThreadLocalRandom.current().nextInt(0, ENTITIES.length)];
        _qty = ThreadLocalRandom.current().nextInt(0, 10000);
    }

    public String getEntity() {
        return _entity;
    }

    public int getQty() {
        return _qty;
    }

    @Override
    public String toString() {
        return "Position [_entity=" + _entity + ", _qty=" + _qty + "]";
    }

}
