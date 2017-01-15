package com.logicalpanda.geoshare;

import junit.framework.Assert;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void float_comparrison_isCorrect() throws Exception {

        double distanceToRefresh = Double.parseDouble("0.02")/2;

        double lastSearchLat = 53.38738589;
        double lastLatMinusDist = lastSearchLat - distanceToRefresh;
        double lastLatPlusDist = lastSearchLat + distanceToRefresh;

        if(lastSearchLat < lastLatMinusDist || lastSearchLat > lastLatPlusDist)
        {
            Assert.fail();
        }
    }
}