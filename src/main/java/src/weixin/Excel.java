package src.weixin;

import net.sf.json.JSONArray;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import src.cron.Task;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by caoshibin on 2018/3/14.
 */
@RestController
class Excel {
    @Resource
    private Task task;
    @RequestMapping("/excel")
    public String index() {
        System.out.println("index");
        String result= task.generateExcel();
        Map<String,String> map=new HashMap<>();
        map.put("status",result);
        JSONArray jsonObject = JSONArray.fromObject(map);
        return jsonObject.toString();
    }
//    @RequestMapping("/function/")
//    public String index() {
//        System.out.println("index");
//        String result= task.generateExcel();
//        Map<String,String> map=new HashMap<>();
//        map.put("status",result);
//        JSONArray jsonObject = JSONArray.fromObject(map);
//        return jsonObject.toString();
//    }
    @RequestMapping(value = "/function/{tjORdl}/{function}")
    @ResponseBody
    public String reload(@PathVariable String tjORdl, @PathVariable String function) {
        System.out.println(tjORdl);
        System.out.println(function);
        task.genetateExcel(function,tjORdl);
        return "success";
    }

}
