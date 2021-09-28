package au.com.cosight.excel;

import au.com.cosight.common.dto.plugin.CosightExecutionContext;
import au.com.cosight.common.dto.plugin.PluginParameterUtils;
import au.com.cosight.common.dto.plugin.PluginParameterValue;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
class PluginAppTest {

    @Autowired
    private CosightExecutionContext ctx;
    @Test
    void loadContext(){
        assertNotNull(ctx.getParameters());
        PluginParameterValue es = PluginParameterUtils.getValue(ctx.getParameters(),"entities");
        assertFalse(es.isEmpty());

        PluginParameterValue qs = PluginParameterUtils.getValue(ctx.getParameters(),"queries");
        assertFalse(qs.isEmpty());


        PluginParameterValue dl = PluginParameterUtils.getValue(ctx.getParameters(),"driveLocation");
        assertFalse(dl.isEmpty());



        assertTrue(true);
    }
}
