package simulator;

import graph.Flow;
import java.util.Collection;

public interface ISwitch {
    int getId();
    boolean addFlow(Flow f);
    boolean removeFlow(Flow f);
    Collection<Port> getPorts();   
}