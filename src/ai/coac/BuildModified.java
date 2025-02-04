/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.coac;

import ai.abstraction.AbstractAction;
import ai.abstraction.pathfinding.PathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import util.XMLWriter;

/**
 * @author santi
 */
public class BuildModified extends AbstractAction {
    UnitType type;
    Unit unit;
    int x, y;
    PathFinding pf;

    public BuildModified(Unit u, UnitType a_type, int a_x, int a_y, PathFinding a_pf) {
        super(u);
        unit = u;
        type = a_type;
        x = a_x;
        y = a_y;
        pf = a_pf;
    }

    public boolean completed(GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit u = pgs.getUnitAt(x, y);
        return u != null;
    }


    public boolean equals(Object o) {
        if (!(o instanceof BuildModified)) return false;
        BuildModified a = (BuildModified) o;
        return type == a.type && x == a.x && y == a.y && pf.getClass() == a.pf.getClass();
    }


    public void toxml(XMLWriter w) {
        w.tagWithAttributes("Build", "unitID=\"" + unit.getID() + "\" type=\"" + type.name + "\" x=\"" + x + "\" y=\"" + y + "\" pathfinding=\"" + pf.getClass().getSimpleName() + "\"");
        w.tag("/Build");
    }

    public UnitAction execute(GameState gs, ResourceUsage ru) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
//        System.out.println("findPathToAdjacentPosition to " + unit.getX() + "," + unit.getY() + " from Build: (" + x + "," + y + ")");
        UnitAction move = pf.findPathToAdjacentPosition(unit, x + y * pgs.getWidth(), gs, ru);
//        System.out.println("Move: " + move);
        if (move != null) {
            if (gs.isUnitActionAllowed(unit, move)) return move;
            return new UnitAction(UnitAction.TYPE_NONE, 1);
        }

        // build:
        UnitAction ua = null;
        if (x == unit.getX() &&
                y == unit.getY() - 1) ua = new UnitAction(UnitAction.TYPE_PRODUCE, UnitAction.DIRECTION_UP, type);
        if (x == unit.getX() + 1 &&
                y == unit.getY()) ua = new UnitAction(UnitAction.TYPE_PRODUCE, UnitAction.DIRECTION_RIGHT, type);
        if (x == unit.getX() &&
                y == unit.getY() + 1) ua = new UnitAction(UnitAction.TYPE_PRODUCE, UnitAction.DIRECTION_DOWN, type);
        if (x == unit.getX() - 1 &&
                y == unit.getY()) ua = new UnitAction(UnitAction.TYPE_PRODUCE, UnitAction.DIRECTION_LEFT, type);
        if (ua != null && gs.isUnitActionAllowed(unit, ua)) return ua;

        // Failure usually seems to be that we simply don't have enough resources anymore
        //System.err.println("Build.execute: something weird just happened " + unit + " builds at " + x + "," + y);
        return new UnitAction(UnitAction.TYPE_NONE, 1);
    }
}
