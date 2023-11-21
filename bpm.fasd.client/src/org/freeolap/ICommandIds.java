package org.freeolap;

/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

    public static final String CMD_OPEN = "org.freeolap.open"; //$NON-NLS-1$
    public static final String CMD_OPEN_MESSAGE = "org.freeolap.openMessage"; //$NON-NLS-1$
    
}
