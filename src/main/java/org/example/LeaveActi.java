package org.example;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveActi {
    /**
     * 会默认按照Resources目录下的activiti.cfg.xml创建流程引擎
     */
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    @Test
    public void test() {
        //读对应的配置文件，初始化
        testCreateProcessEngineByCfgXml();
        //部署
        deployProcess();
        //启动
        startProcess();
        //查看
        queryTask();
        //处理
        handleTask();
    }

    /**
     * 根据配置文件activiti.cfg.xml创建ProcessEngine
     */
    @Test
    public void testCreateProcessEngineByCfgXml() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        ProcessEngine engine = cfg.buildProcessEngine();
    }

    /**
     * 发布流程
     * RepositoryService
     */
    @Test
    public void deployProcess() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment(); //创建部署对象
        builder.name("请假审批流程"); //流程名称
        builder.addClasspathResource("testLeave.xml"); //加载资源文件    xml发布有问题
        builder.addClasspathResource("testLeave.png");
        Deployment deploy = builder.deploy();
        System.out.println("流程部署的ID: "+deploy.getId());
    }

    /**
     * 启动流程
     * RuntimeService
     */
    @Test
    public void startProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        ProcessInstance leaveInstance = runtimeService.startProcessInstanceByKey("leaveProcess");

        System.out.println("流程id: "+leaveInstance.getId());
    }

    /**
     * 查看任务
     * TaskService
     */
    @Test
    public void queryTask() {
        TaskService taskService = processEngine.getTaskService();
        //根据assignee(代理人)查询任务
//        String assignee = "项目经理";
//        String assignee = "组长";
        String assignee = "张三";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        System.out.println("任务数量："+tasks.size());
        int size = tasks.size();
        for (int i = 0; i < size; i++) {
            Task task = tasks.get(i);

        }
        for (Task task : tasks) {

            System.out.println("taskId:" + task.getId() +
                    ",taskName:" + task.getName() +
                    ",assignee:" + task.getAssignee() +
                    ",createTime:" + task.getCreateTime());
        }
    }

    /**
     * 办理任务
     */
    @Test
    public void handleTask() {
        TaskService taskService = processEngine.getTaskService();

        Map<String, Object> variables = new HashMap<>();
        variables.put("day", 1);  // 写申请流程变量
        variables.put("action","同意"); //审批流程变量  （驳回/同意）

        //根据上一步生成的taskId执行任务
        String taskId = "2508";//5005
        taskService.complete(taskId, variables);
        System.out.println("任务完成");
    }
}
