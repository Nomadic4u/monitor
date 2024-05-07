package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.dto.Client;
import org.example.entity.vo.request.ClientDetailVO;
import org.example.entity.vo.request.RuntimeDetailVO;
import org.example.service.ClientService;
import org.example.utils.Const;
import org.springframework.web.bind.annotation.*;

/**
 * 用于客户机发送HTTP请求
 */
@RestController
@RequestMapping("/monitor")
public class ClientController {

    @Resource
    ClientService clientService;

    @GetMapping("/register")
    public RestBean<Void> registerClient(@RequestHeader("Authorization") String token) {
        return clientService.verifyAndRegister(token) ?
                RestBean.success() : RestBean.failure(401, "token无效, 注册失败");
    }

    @PostMapping("/detail")
    public RestBean<Void> updateClientDetails(@RequestAttribute(Const.ATTR_CLIENT) Client client,
                                              @RequestBody @Valid ClientDetailVO vo) {
        clientService.updateClientDetails(vo, client);
        return RestBean.success();
    }

    @PostMapping("/runtime")
    public RestBean<Void> updateRuntimeDetails(@RequestAttribute(Const.ATTR_CLIENT) Client client,
                                               @RequestBody @Valid RuntimeDetailVO vo) {
        clientService.updateRuntimeDetails(vo, client);
        return RestBean.success();
    }

}
