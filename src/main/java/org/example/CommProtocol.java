package org.example;

/**
 * On this enum relies the communication protocol.
 * Each enum corresponds to the state of client's FSM
 * QUERY -> information requiring confirmation
 * RECEIVE -> information to be only displayed
 * MOVE -> information that needs a confirmation in a form of a move
 * NULLCOMMAND -> default state when
 * ... etc ...
 * communication doesn't occur
 */
public enum CommProtocol {
        QUERY,
        RECEIVE,
        WAIT,
        MOVE,
        END,
        NULLCOMMAND
}
