package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.RuntimeDetailVO;
import org.example.entity.vo.response.ClientDetailsVO;
import org.example.entity.vo.request.RenameClientVO;
import org.example.entity.vo.request.RenameNodeVO;
import org.example.entity.vo.response.ClientPreviewVO;
import org.example.entity.vo.response.ClientSimpleVO;
import org.example.entity.vo.response.RuntimeHistoryVO;
import org.example.service.AccountService;
import org.example.service.ClientService;
import org.example.utils.Const;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用来与前端交互
 */
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Resource
    ClientService service;

    @Resource
    AccountService accountService;

    @GetMapping("/list")
    public RestBean<List<ClientPreviewVO>> listAllClient(@RequestAttribute(Const.ATTR_USER_ID) int userID,
                                                         @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        List<ClientPreviewVO> list = service.listAllClient(); // 先获取所有的客户端
        if(this.isAdminAccount(userRole))
            return RestBean.success(list);
        List<Integer> ids = this.accountAccessClient(userID);
        return RestBean.success(list.stream()
                .filter(vo -> ids.contains(vo.getId())) // 这里采用过滤器, 去除用户没有权限去访问的主机
                .toList());
    }

    @GetMapping("/simple-list")
    public RestBean<List<ClientSimpleVO>> simpleClientList() {
        return RestBean.success(service.listSimpleList());
    }

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestBody @Valid RenameClientVO vo) {
        service.renameClient(vo);
        return RestBean.success();
    }

    @PostMapping("/node")
    public RestBean<Void> renameNode(@RequestBody @Valid RenameNodeVO vo) {
        service.renameNode(vo);
        return RestBean.success();
    }


    @GetMapping("/details")
    public RestBean<ClientDetailsVO> details(int clientId) {
        return RestBean.success(service.clientDetails(clientId));
    }

    @GetMapping("/runtime-history")
    public RestBean<RuntimeHistoryVO> runtimeDetailsHistory(int clientId) {
        return RestBean.success(service.clientRuntimeDetailsHistory(clientId));
    }

    @GetMapping("/runtime-now")
    public RestBean<RuntimeDetailVO> runtimeDetailsNow(int clientId) {
        return RestBean.success(service.clientRuntimeDetailsNow(clientId));
    }

    @GetMapping("/register")
    public RestBean<String> registerToken() {
        return RestBean.success(service.registerTOken());
    }

    @GetMapping("/delete")
    public RestBean<String> deleteClient(int clientId) {
        service.deleteClient(clientId);
        return RestBean.success();
    }

    private List<Integer> accountAccessClient(int uid) {
        Account account = accountService.getById(uid);
        return account.getClientList();
    }

    /**
     * 判断是否为管理员账户
     */
    private boolean isAdminAccount(String role) {
        // Spring的role一般带有前缀"ROLE_", 这里要先去掉
        role = role.substring(5);
        return Const.ROLE_ADMIN.equals(role);
    }

}


















