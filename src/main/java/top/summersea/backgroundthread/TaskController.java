package top.summersea.backgroundthread;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.summersea.backgroundthread.infrastructure.BackgroundThreadPoolExecutor;
import top.summersea.backgroundthread.infrastructure.DelayRunnable;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("task")
public class TaskController {

    @GetMapping("existence")
    public ResponseEntity<?> isControllerAccessible() {
        return ResponseEntity.ok("existence");
    }

    @PostMapping()
    public ResponseEntity<?> registerTask(@RequestBody TaskVO taskVO) {
        if (!taskVO.isParamAvailable()) {
            return ResponseEntity.badRequest().build();
        }
        BackgroundThreadPoolExecutor.registerScheduleTask(new DelayRunnable(taskVO.getName()) {
            @Override
            public void run() {
                System.out.println(taskVO.getContent());
            }
        }, 1, TimeUnit.SECONDS);
        return ResponseEntity.ok().build();
    }
}
