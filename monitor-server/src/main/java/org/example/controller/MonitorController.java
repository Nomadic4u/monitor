package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.vo.request.RenameClientVo;
import org.example.entity.vo.response.ClientPreviewVo;
import org.example.service.ClientService;
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

    @GetMapping("/list")
    public RestBean<List<ClientPreviewVo>> listAllClient() {
        return RestBean.success(service.listAllClient());
    }

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestBody @Valid RenameClientVo vo) {
        return RestBean.success();
    }
}
