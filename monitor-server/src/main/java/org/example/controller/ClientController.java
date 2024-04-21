package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.dto.Client;
import org.example.entity.vo.request.ClientDetailVo;
import org.example.service.ClientService;
import org.example.utils.Const;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monitor")
public class ClientController {

    @Resource
    ClientService clientService;

    @GetMapping("/register")
    public RestBean<Void> registerClient(@RequestHeader("Authorization") String token) {
        return clientService.verifyAndRegister(token) ? RestBean.success() : RestBean.failure(401, "token无效, 注册失败");
    }

    @PostMapping("/detail")
    public RestBean<Void> updateClientDetails(@RequestAttribute(Const.ATTR_CLIENT)Client client,
                                              @RequestBody @Valid ClientDetailVo vo) {
        clientService.updateClientDetails(vo,  client);
        return RestBean.success();
    }

}
