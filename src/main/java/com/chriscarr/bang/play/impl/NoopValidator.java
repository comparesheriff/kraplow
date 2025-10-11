package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayValidator;
import com.chriscarr.bang.play.ValidationResult;
import com.chriscarr.bang.turn.TurnContext;

public class NoopValidator implements PlayValidator {
    @Override
    public ValidationResult validate(TurnContext ctx, PlayCommand cmd) {
        return ValidationResult.OK;
    }
}
