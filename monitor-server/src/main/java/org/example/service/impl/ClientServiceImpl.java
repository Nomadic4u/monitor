package org.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.example.entity.dto.Client;
import org.example.entity.dto.ClientDetail;
import org.example.entity.vo.request.ClientDetailVo;
import org.example.entity.vo.request.RenameClientVo;
import org.example.entity.vo.request.RuntimeDetailVo;
import org.example.entity.vo.response.ClientPreviewVo;
import org.example.mapper.ClientDetailMapper;
import org.example.mapper.ClientMapper;
import org.example.service.ClientService;
import org.example.utils.InfluxDbUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Wrapper;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    private String registerToken = this.generateNewToken();

    private final Map<Integer, Client> clientIdCache = new ConcurrentHashMap<>();

    private final Map<String, Client> clientTokenCache = new ConcurrentHashMap<>();

    @Resource
    ClientDetailMapper clientDetailMapper;

    @Resource
    InfluxDbUtils influxDbUtils;

    /**
     * 这个方法会在ClientServiceImpl被创建后执行
     */
    @PostConstruct
    public void initClientCache() {
        this.list().forEach(this::addClientCache);
    }


    @Override
    public boolean verifyAndRegister(String token) {
        if (this.registerToken.equals(token)) {
            int id = this.randomClientId();
            Client client = new Client(id, "未命名主机", token, "cn", "未命名节点",  new Date());
            if (this.save(client)) {
                registerToken = this.generateNewToken(); // 重新生成一下token
                this.addClientCache(client);
                return true;
            }
        }
        return false;
    }

    @Override
    public Client findClientById(int id) {
        return clientIdCache.get(id);
    }

    @Override
    public Client findClientByToken(String token) {
        return clientTokenCache.get(token);
    }

    @Override
    public String registerTOken() {
        return registerToken;
    }

    // 更新客户端的信息
    @Override
    public void updateClientDetails(ClientDetailVo vo, Client client) {
        ClientDetail detail = new ClientDetail();
        BeanUtils.copyProperties(vo, detail);
        detail.setId(client.getId());
        if (Objects.nonNull(clientDetailMapper.selectById(client.getId()))) {
            clientDetailMapper.updateById(detail);
        } else {
            clientDetailMapper.insert(detail); // 没有的话就直接插入
        }
    }


    // 存贮最近时间的运行时信息
    private Map<Integer, RuntimeDetailVo> currentRuntime = new ConcurrentHashMap<>();

    // 数据存入influxdb
    @Override
    public void updateRuntimeDetails(RuntimeDetailVo vo, Client client) {
        currentRuntime.put(client.getId(), vo);
        influxDbUtils.writeRuntimeData(client.getId(), vo);
    }

    @Override
    public List<ClientPreviewVo> listAllClient() {

        return clientIdCache.values().stream().map(client -> {
            ClientPreviewVo vo = client.asViewObject(ClientPreviewVo.class);
            BeanUtils.copyProperties(clientDetailMapper.selectById(vo.getId()), vo); // 缺失的数据再从数据库中查询
            RuntimeDetailVo runtimeDetailVo = currentRuntime.get(client.getId());
            // 如果最后一次更新的时间大于60秒, 说明已经离线了
            if(runtimeDetailVo != null && System.currentTimeMillis() - runtimeDetailVo.getTimestamp() < 60 * 1000) {
                BeanUtils.copyProperties(runtimeDetailVo, vo);
                vo.setOnline(true);
            }
            return vo;
        }).toList();
    }

    /**
     * 服务器改名
     * @param vo
     */
    @Override
    public void renameClient(RenameClientVo vo) {
        this.update(Wrappers.<Client>update().eq("id", vo.getId()).set("name", vo.getName()));
        this.initClientCache();
    }

    private void addClientCache(Client client) {
        clientIdCache.put(client.getId(), client);
        clientTokenCache.put(client.getToken(), client);
    }

    /**
     * 生成随机的clientId
     *
     * @return
     */
    private int randomClientId() {
        return new Random().nextInt(90000000) + 10000000;
    }

    /**
     * 生成新的token
     *
     * @return 生成的token
     */
    private String generateNewToken() {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLNMOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(24);
        for (int i = 0; i < 24; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        System.out.println(sb);
        return sb.toString();
    }
}
