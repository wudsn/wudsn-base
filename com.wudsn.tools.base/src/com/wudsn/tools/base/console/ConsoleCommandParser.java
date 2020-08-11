/**
 * Copyright (C) 2013 - 2014 <a href="http://www.wudsn.com" target="_top">Peter Dell</a>
 *
 * This file is part of a WUDSN software distribution.
 * 
 * The!Cart Studio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The!Cart Studio distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the WUDSN software distribution. If not, see <http://www.gnu.org/licenses/>.
 */
package com.wudsn.tools.base.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import com.wudsn.tools.base.Messages;
import com.wudsn.tools.base.common.MessageQueue;
import com.wudsn.tools.base.common.StringUtility;
import com.wudsn.tools.base.repository.Action;

public final class ConsoleCommandParser {

    private String mainClassPath;
    private Console console;
    private List<ConsoleCommand> commands;

    private List<ConsoleCommandExecution> parseResult;

    public ConsoleCommandParser(String mainClassPath) {
	if (mainClassPath == null) {
	    throw new IllegalArgumentException("Parameter 'mainClassPath' must not be null.");
	}
	this.mainClassPath = mainClassPath;
	commands = new ArrayList<ConsoleCommand>();
    }

    public ConsoleCommand addCommand(Action action, String actionCommand) {
	if (action == null) {
	    throw new IllegalArgumentException("Parameter 'action' must not be null.");
	}
	if (actionCommand == null) {
	    throw new IllegalArgumentException("Parameter 'actionCommand' must not be null.");
	}
	ConsoleCommand result = new ConsoleCommand(action, actionCommand);
	commands.add(result);
	return result;
    }

    /**
     * Parses the command line arguments.
     * 
     * @param args
     *            The command line arguments, may be empty, not
     *            <code>null</code>.
     * 
     * @param mandatory
     *            <code>true</code> if specifying an argument is mandatory.
     * 
     * @param messageQueue
     *            The message queue, not <code>null</code>.
     */
    public void parse(String[] args, boolean mandatory, MessageQueue messageQueue) {
	if (args == null) {
	    throw new IllegalArgumentException("Parameter 'args' must not be null.");
	}
	if (messageQueue == null) {
	    throw new IllegalArgumentException("Parameter 'messageQueue' must not be null.");
	}
	console = new Console();

	boolean printHelp = false;
	parseResult = new ArrayList<ConsoleCommandExecution>();
	if (args.length == 0 && mandatory) {
	    printHelp = true;
	} else {
	    for (int i = 0; i < args.length; i++) {
		String arg = args[i];
		if (arg.length() > 0) {
		    if (arg.startsWith("-")) {
			arg = arg.substring(1);
			if (arg.equalsIgnoreCase("help")) {
			    printHelp = true;
			} else {
			    String commandName;
			    boolean parametersSpecified;
			    int index = arg.indexOf(":");
			    if (index >= 0) {
				commandName = arg.substring(0, index);
				parametersSpecified = true;
			    } else {
				commandName = arg;
				parametersSpecified = false;
			    }
			    ConsoleCommand command = null;
			    for (int j = 0; j < commands.size() && command == null; j++) {
				ConsoleCommand possibleCommand = commands.get(j);
				if (possibleCommand.getActionCommand().equalsIgnoreCase(commandName)) {
				    command = possibleCommand;
				}
			    }
			    if (command == null) {
				// ERROR: Unknown command '{0}'.
				messageQueue.sendMessage(this, null, Messages.E251, commandName);
			    } else {
				Map<String, List<String>> parameterValues = new TreeMap<String, List<String>>();
				if (command.getParameters().isEmpty()) {
				    if (parametersSpecified) {
					// ERROR: Command '{0}' does not have
					// parameters.
					messageQueue.sendMessage(this, null, Messages.E252, commandName);
				    }
				} else {
				    if (!parametersSpecified) {
					// ERROR:No parameters specified for command '{0}'.
					messageQueue.sendMessage(this, null, Messages.E253, commandName);
				    } else {
					ConsoleCommandParameter parameter = command.getParameters().get(0);
					String parameterValue = arg.substring(index + 1).trim();
					List<String> parameterValueStrings = new ArrayList<String>();

					// Remove surrounding double quotes.
					if (parameterValue.length() >= 2 && parameterValue.startsWith("\"")
						&& parameterValue.endsWith("\"")) {
					    parameterValue = parameterValue.substring(1, parameterValue.length() - 1);
					}

					// Ignore empty parameter values by now.
					if (StringUtility.isSpecified(parameterValue)) {
					    parameterValueStrings.add(parameterValue);
					    parameterValues.put(parameter.getAttribute().getName(),
						    parameterValueStrings);
					}
				    }
				}
				if (!printHelp) {
				    parseResult.add(new ConsoleCommandExecution(command, parameterValues));
				}
			    }
			}
		    } else {
			// ERROR: Argument '{0}' does not start with a dash.
			messageQueue.sendMessage(this, null, Messages.E250, arg);
			printHelp = true;
		    }
		}
	    }
	}
	console.displayMessageQueue(messageQueue);
	
	printHelp |= messageQueue.containsError();
	if (printHelp) {
	    printHelp();
	    console.exit(false);
	}
    }

    public Console getConsole() {
	if (console == null) {
	    throw new IllegalStateException("No console present. Parse arguments first.");
	}
	return console;
    }

    public List<ConsoleCommandExecution> getParseResult() {
	if (parseResult == null) {
	    throw new IllegalStateException("Nothing parsed yet");
	}
	return parseResult;
    }

    public void printHelp() {
	StringBuilder helpBuilder = new StringBuilder();
	helpBuilder.append("Usage: java ").append(mainClassPath).append(" [-command_1] ... [-command_n]\n\n");

	int maximumActionCommandLength = 0;
	for (ConsoleCommand command : commands) {
	    int length = command.getActionCommand().length();
	    for (ConsoleCommandParameter parameter : command.getParameters()) {
		length += parameter.getAttribute().getName().length() + 3;
	    }
	    maximumActionCommandLength = Math.max(maximumActionCommandLength, length);
	}

	StringBuilder builder = new StringBuilder(maximumActionCommandLength);
	for (ConsoleCommand command : commands) {
	    builder.setLength(0);
	    builder.append(command.getActionCommand());
	    for (ConsoleCommandParameter parameter : command.getParameters()) {
		builder.append(":<");
		builder.append(parameter.getAttribute().getName());
		builder.append(">");
	    }
	    while (builder.length() < maximumActionCommandLength + 1) {
		builder.append(' ');
	    }
	    String helpText = command.getAction().getToolTip();
	    if (StringUtility.isEmpty(helpText)) {
		helpText = command.getAction().getLabelWithoutMnemonics();
	    }
	    helpBuilder.append("-").append(builder.toString()).append(helpText).append("\n");
	}
	if (System.console() == null) {
	    JOptionPane.showMessageDialog(null, helpBuilder.toString());
	} else {
	    console.println(helpBuilder.toString());
	}
    }

}
