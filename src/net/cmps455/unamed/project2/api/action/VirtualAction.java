package net.cmps455.unamed.project2.api.action;

import java.util.ArrayList;

/**
 * Represents a VirtualAction on a {@link net.cmps455.unamed.project2.api.VirtualObject VirtualObject}
 */
public class VirtualAction {

    private static int counter = 1;
    /**
     * Empty Virtual Action
     */
    private static final VirtualAction NONE = new VirtualAction();
    private final int id;
    private final VirtualAction[] children;
    /** Name of the {@link VirtualAction} */
    public final String name;

    private VirtualAction() {
        this.id = 0;
        this.children = null;
        this.name = "";
    }

    /**
     * Constructor for VirtualAction without children.
     * @param name name of action.
     * @see VirtualAction#VirtualAction(String, VirtualAction...)
     * Constructor for VirtualAction with children
     */
    public VirtualAction(String name) {
        this.name = name;
        this.id = counter++;
        this.children = null;
    }

    /**
     * Constructor for VirtualAction with children.
     * <p>
     * If the child {@link VirtualAction#has has} this action
     * or if the action is {@link VirtualAction#NONE empty} then
     * it will not be added.
     * @param name name of action.
     * @param children array of children.
     * @see VirtualAction#VirtualAction(String)
     * Constructor for VirtualAction without children.
     */
    public VirtualAction(String name, VirtualAction ... children) {
        this.name = name;
        this.id = counter++;
        if (children == null || children.length == 0) {
            this.children = null;
        } else {
            // We don't welcome circular references here
            ArrayList<VirtualAction> valid = new ArrayList<>();
            for (VirtualAction child : children) {
                if (!child.has(this)) {
                    valid.add(child);
                }
            }
            this.children = valid.toArray(new VirtualAction[0]);
        }
    }

    /**
     * Determines if this VirtualAction is equal to or possesses another virtual action.
     * <p>
     * <i> Note: </i>
     * <ul>
     *   <li> Using '{@code .has(VirtualAction.NONE)}' is equivalent to using
     *   '{@link VirtualAction#isEmpty() .isEmpty()}' </li>
     *   <li> No action (besides the {@link VirtualAction#NONE empty} action) will contain the
     *   {@link VirtualAction#NONE empty} action. </li>
     * </ul>
     * </p>
     * @param action the action to compare.
     * @return {@code true} if contains action; {@code false} otherwise.
     */
    public boolean has(VirtualAction action) {
        // This is the action
        if (action.equals(this))
            return true;

        // This or the action is empty
        if (isEmpty() || action.isEmpty()) return false;

        // No children, so this does not contain action
        if (children == null) {
            return false;
        }

        // Is the action a child of this?
        for (VirtualAction child : children) {
            if (child.has(action)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Is this action the {@link VirtualAction#NONE empty} action.
     * @return {@code true} if this is the empty action; {@code false} otherwise.
     */
    public boolean isEmpty() {
        return id == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VirtualAction other))
            return false;

        return this.id == other.id;
    }
}
