package com.chriscarr.bang.play;

import com.chriscarr.bang.play.impl.NoopParser;
import com.chriscarr.bang.play.impl.NoopResolver;
import com.chriscarr.bang.play.impl.NoopValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayPipelineSmokeTest {

    @Test
    @Timeout(1)
    void noopPipeline_emitsPass_and_acceptsIt() {
        PlayParser noopParser = new NoopParser();
        PlayValidator noopValidator = new NoopValidator();
        PlayResolver noopResolver = new NoopResolver();
        PlayCommand cmd = noopParser.parse(null);
        assertEquals(PlayCommand.Type.PASS, cmd.type());

        ValidationResult result = noopValidator.validate(null, cmd);
        assertTrue(result.valid());

        noopResolver.resolve(null, cmd);

    }

}
