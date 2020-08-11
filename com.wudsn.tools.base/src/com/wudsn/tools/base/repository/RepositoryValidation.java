package com.wudsn.tools.base.repository;

import com.wudsn.tools.base.Messages;
import com.wudsn.tools.base.common.MessageQueue;
import com.wudsn.tools.base.common.StringUtility;
import com.wudsn.tools.base.common.TextUtility;

public final class RepositoryValidation {

    private MessageQueue messageQueue;

    private RepositoryValidation() {

    }

    public static RepositoryValidation createInstance(MessageQueue messageQueue) {
	if (messageQueue == null) {
	    throw new IllegalArgumentException("Parameter 'messageQueue' must not be null.");
	}
	RepositoryValidation result = new RepositoryValidation();
	result.messageQueue = messageQueue;
	return result;
    }

    public boolean isStringSpecified(Object owner, Attribute attribute, String stringValue) {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}
	if (StringUtility.isEmpty(stringValue)) {
	    // ERROR: {0} must be specified.
	    messageQueue.sendMessage(owner, attribute, Messages.E300, attribute.getDataType()
		    .getLabelWithoutMnemonics());
	    return false;
	}
	return true;
    }

    public boolean isStringDefined(Object owner, Attribute attribute, String stringValue, boolean defined) {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}
	if (StringUtility.isSpecified(stringValue) && !defined) {
	    // ERROR: {0} '{1}' is not defined.
	    messageQueue.sendMessage(owner, attribute, Messages.E301, attribute.getDataType()
		    .getLabelWithoutMnemonics(), stringValue);
	    return false;
	}
	return true;
    }

    public boolean isStringValid(Object owner, Attribute attribute,
	    String stringValue) {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}

	if (!isStringSpecified(owner, attribute, stringValue)) {
	    return false;
	}
	int maximumLength = attribute.getDataType().getMaximumLength();
	if (stringValue.length() >maximumLength) {
	    // ERROR: {0} has {1} characters and is longer than the maximum
	    // allowed length of {2} characters.
	    messageQueue.sendMessage(owner, attribute, Messages.E302, attribute.getDataType()
		    .getLabelWithoutMnemonics(), TextUtility.formatAsDecimal(stringValue.length()), TextUtility
		    .formatAsDecimal(maximumLength));
	    return false;
	}
	String allowedCharacters=attribute.getDataType().getAllowedCharacters();
	if (allowedCharacters != null) {
	    for (int i = 0; i < stringValue.length(); i++) {
		char c = stringValue.charAt(i);
		if (allowedCharacters.indexOf(c) < 0) {
		    // {0} contains the invalid character '{1}' at position {2}.
		    // Allowed characters are '{3}'.
		    messageQueue.sendMessage(owner, attribute, Messages.E303, attribute.getDataType()
			    .getLabelWithoutMnemonics(), Character.toString(c), TextUtility.formatAsDecimal(i + 1),
			    allowedCharacters);
		    return false;
		}
	    }
	}
	return true;
    }

    public boolean isLongValid(Object owner, Attribute attribute, long minimumValue, long maximumValue, long longValue) {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}
	if (longValue < minimumValue || longValue > maximumValue) {
	    // ERROR: {0} has value {1} and is not between {2} and {3}.
	    messageQueue.sendMessage(owner, attribute, Messages.E304, attribute.getDataType()
		    .getLabelWithoutMnemonics(), TextUtility.formatAsDecimal(longValue), TextUtility
		    .formatAsDecimal(minimumValue), TextUtility.formatAsDecimal(maximumValue));
	    return false;
	}
	return true;
    }

    public boolean isMemorySizeValid(Object owner, Attribute attribute, long minimumValue, long maximumValue,
	    long longValue) {
	if (attribute == null) {
	    throw new IllegalArgumentException("Parameter 'attribute' must not be null.");
	}
	if (longValue < minimumValue || longValue > maximumValue) {
	    // ERROR: {0} has value {1} and is not between {2} and {3}.
	    messageQueue.sendMessage(owner, attribute, Messages.E304, attribute.getDataType()
		    .getLabelWithoutMnemonics(), TextUtility.formatAsMemorySize(longValue), TextUtility
		    .formatAsDecimal(minimumValue), TextUtility.formatAsMemorySize(maximumValue));
	    return false;
	}
	return true;
    }
}
