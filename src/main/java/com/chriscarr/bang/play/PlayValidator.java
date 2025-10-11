package com.chriscarr.bang.play;

import com.chriscarr.bang.turn.TurnContext;

public interface PlayValidator {
    ValidationResult validate(TurnContext ctx, PlayCommand cmd);
}
