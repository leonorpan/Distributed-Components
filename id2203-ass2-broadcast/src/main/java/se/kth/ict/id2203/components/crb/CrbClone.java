package se.kth.ict.id2203.components.crb;
 
import java.util.Arrays;
 
import se.kth.ict.id2203.ports.crb.CrbDeliver;
import se.sics.kompics.address.Address;
 
public class CrbClone {
 
    private Address source;
    private int[] vector;
    private CrbDeliver deliver;
 
    public CrbClone(Address source, int[] vector, CrbDeliver deliver) {
        this.source = source;
        this.vector = vector;
        this.deliver = deliver;
    }
 
    public Address getSource() {
        return source;
    }
 
    public int[] getVector() {
        return vector;
    }
 
    public CrbDeliver getDeliver() {
        return deliver;
    }
 
    @Override
    public String toString() {
        return "CrbObject [source=" + source + ", vector="
                + Arrays.toString(vector) + ", message=" + deliver + "]";
    }
 
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(vector);
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
        CrbClone other = (CrbClone) obj;
        if (!Arrays.equals(vector, other.vector))
            return false;
        return true;
    }
 
}