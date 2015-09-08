package br.com.comoqueta.comoquetasaude.android.models;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 19/08/2015
 * @since #
 */
public class ModelsTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(ModelsTestSuite.class)
                .includeAllPackagesUnderHere()
                .build();
    }
}
