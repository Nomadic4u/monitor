package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Client;
import org.example.entity.vo.request.*;
import org.example.entity.vo.response.ClientDetailsVO;
import org.example.entity.vo.response.ClientPreviewVO;
import org.example.entity.vo.response.ClientSimpleVO;
import org.example.entity.vo.response.RuntimeHistoryVO;

import java.util.List;

public interface ClientService extends IService<Client> {

    boolean verifyAndRegister(String token);

    Client findClientById(int id);

    Client findClientByToken(String token);

    String registerTOken();

    void updateClientDetails(ClientDetailVO vo, Client client);

    void updateRuntimeDetails(RuntimeDetailVO vo, Client client);

    List<ClientPreviewVO> listAllClient();

    void renameClient(RenameClientVO vo);

    void renameNode(RenameNodeVO vo);

    ClientDetailsVO clientDetails(int clientId);

    // 获取近一个小时的服务器数据
    RuntimeHistoryVO clientRuntimeDetailsHistory(int clientId);

    // 获取当前的服务器数据
    RuntimeDetailVO clientRuntimeDetailsNow(int clientId);

    void deleteClient(int clientId);

    List<ClientSimpleVO> listSimpleList();
}
