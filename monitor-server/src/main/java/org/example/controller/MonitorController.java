package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.RuntimeDetailVO;
import org.example.entity.vo.request.SshConnectionVO;
import org.example.entity.vo.response.*;
import org.example.entity.vo.request.RenameClientVO;
import org.example.entity.vo.request.RenameNodeVO;
import org.example.service.AccountService;
import org.example.service.ClientService;
import org.example.utils.Const;
import org.springframework.security.access.prepost.PostAuthorize;
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
        if (this.isAdminAccount(userRole))
            return RestBean.success(list);
        List<Integer> ids = this.accountAccessClient(userID);
        return RestBean.success(list.stream()
                .filter(vo -> ids.contains(vo.getId())) // 这里采用过滤器, 去除用户没有权限去访问的主机
                .toList());
    }

    @GetMapping("/simple-list")
    public RestBean<List<ClientSimpleVO>> simpleClientList(@RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole))
            return RestBean.success(service.listSimpleList());
        else {

            return RestBean.noPermission();
        }
    }

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestBody @Valid RenameClientVO vo,
                                       @RequestAttribute(Const.ATTR_USER_ID) int userID,
                                       @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.permissionCheck(userID, userRole, vo.getId())) {
            service.renameClient(vo);
            return RestBean.success();
        }
        return RestBean.noPermission();
    }

    @PostMapping("/node")
    public RestBean<Void> renameNode(@RequestBody @Valid RenameNodeVO vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int userID,
                                     @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.permissionCheck(userID, userRole, vo.getId())) {
            service.renameNode(vo);
            return RestBean.success();
        }
        return RestBean.noPermission();
    }


    @GetMapping("/details")
    public RestBean<ClientDetailsVO> details(int clientId,
                                             @RequestAttribute(Const.ATTR_USER_ID) int userID,
                                             @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.permissionCheck(userID, userRole, clientId)) {
            return RestBean.success(service.clientDetails(clientId));
        }
        return RestBean.noPermission();
    }

    @GetMapping("/runtime-history")
    public RestBean<RuntimeHistoryVO> runtimeDetailsHistory(int clientId,
                                                            @RequestAttribute(Const.ATTR_USER_ID) int userID,
                                                            @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        System.out.println("userId: " + userID + " userRole: " + userRole + " clientId: " + clientId);
        if (this.permissionCheck(userID, userRole, clientId)) {
            return RestBean.success(service.clientRuntimeDetailsHistory(clientId));
        }
        return RestBean.noPermission();
    }

    @GetMapping("/runtime-now")
    public RestBean<RuntimeDetailVO> runtimeDetailsNow(int clientId,
                                                       @RequestAttribute(Const.ATTR_USER_ID) int userID,
                                                       @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.permissionCheck(userID, userRole, clientId)) {
            return RestBean.success(service.clientRuntimeDetailsNow(clientId));
        }
        return RestBean.noPermission();
    }

    @GetMapping("/register")
    public RestBean<String> registerToken(@RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole))
            return RestBean.success(service.registerTOken());
        return RestBean.noPermission();
    }

    @GetMapping("/delete")
    public RestBean<String> deleteClient(int clientId,
                                         @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole)) {
            service.deleteClient(clientId);
            return RestBean.success();
        }
        return RestBean.noPermission();
    }

    @PostMapping("/ssh-save")
    public RestBean<Void> saveSshConnection(@RequestBody @Valid SshConnectionVO vo,
                                            @RequestAttribute(Const.ATTR_USER_ID) int userID,
                                            @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.permissionCheck(userID, userRole, vo.getId())) {
            service.saveClientSshConnection(vo);
            return RestBean.success();
        }
        return RestBean.noPermission();
    }

    @GetMapping("/ssh")
    public RestBean<SshSettingVO> sshSettings(int ClientId,
                                              @RequestAttribute(Const.ATTR_USER_ID) int userID,
                                              @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.permissionCheck(userID, userRole, ClientId)) {
            return RestBean.success(service.sshSettings(ClientId));
        }
        return RestBean.noPermission();
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

    /**
     * 判断是否具有权限
     *
     * @param uid
     * @param role
     * @param clientId
     * @return
     */
    private boolean permissionCheck(int uid, String role, int clientId) {
        if (this.isAdminAccount(role))
            return true;
        return this.accountAccessClient(uid).contains(clientId);
    }

}


















