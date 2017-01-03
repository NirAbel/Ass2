package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.tools.Tool;

public class ToolTask extends Task<Tool> {
    Tool tool;
    Warehouse warehouse;

    ToolTask(Warehouse w, Tool t){
        warehouse=w;
        tool=t;
    }

    public void start(){
        warehouse.releaseTool(tool);
    }
}
