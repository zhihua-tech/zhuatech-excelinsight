/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.excelinsight.controller;

import cn.zhuatech.excelinsight.service.DataQualityGovernanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 企业级决策 API，调用方应保存请求、响应与审批审计轨迹。 */
@RestController
@RequestMapping("/api/enterprise/data-quality")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
public class DataQualityGovernanceController {
    private final DataQualityGovernanceService service;

    public DataQualityGovernanceController(DataQualityGovernanceService service) {
        this.service = service;
    }

    @PostMapping("/assess")
    public DataQualityGovernanceService.Assessment decide(@Valid @RequestBody DataQualityGovernanceService.Request request) {
        return service.assess(request);
    }
}
