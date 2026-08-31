/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.excelinsight.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** Excel 数据进入正式报表或业务流程前的数据质量门禁。 */
@Service
public class DataQualityGovernanceService {
    public Assessment assess(Request request) {
        BigDecimal issueRate = BigDecimal.valueOf((request.missingCells() + request.duplicateRows()) * 100L)
                .divide(BigDecimal.valueOf(request.rowCount()), 2, RoundingMode.HALF_UP);
        int riskScore = Math.min(100, issueRate.intValue() * 2 + request.schemaDriftColumns() * 12
                + request.piiColumns() * 8 + (request.ownerAssigned() ? 0 : 15)
                + (request.lineageDocumented() ? 0 : 15));
        boolean publishAllowed = riskScore < 30 && request.ownerAssigned() && request.lineageDocumented()
                && (request.piiColumns() == 0 || request.publishApproved());
        String grade = riskScore < 15 ? "A" : riskScore < 30 ? "B" : riskScore < 60 ? "C" : "D";
        String route = publishAllowed ? "PUBLISH_APPROVED"
                : request.piiColumns() > 0 && !request.publishApproved() ? "PRIVACY_APPROVAL"
                : request.schemaDriftColumns() > 0 ? "SCHEMA_REMEDIATION" : "QUALITY_QUARANTINE";
        List<String> controls = new ArrayList<>();
        if (request.schemaDriftColumns() > 0) controls.add("冻结字段映射并完成模式漂移影响分析");
        if (request.piiColumns() > 0) controls.add("敏感字段执行分级、脱敏和最小权限导出");
        if (!request.ownerAssigned()) controls.add("指定数据责任人后方可发布");
        if (!request.lineageDocumented()) controls.add("补齐来源、清洗规则、版本和下游血缘");
        if (controls.isEmpty()) controls.add("保存质量规则版本、数据快照与发布审批记录");
        return new Assessment(request.datasetId(), issueRate, riskScore, grade, route,
                publishAllowed, List.copyOf(controls));
    }

    public record Request(@NotBlank String datasetId, @Min(1) int rowCount,
                          @Min(0) int missingCells, @Min(0) int duplicateRows,
                          @Min(0) int schemaDriftColumns, @Min(0) int piiColumns,
                          boolean ownerAssigned, boolean lineageDocumented,
                          boolean publishApproved) {}

    public record Assessment(String datasetId, BigDecimal issueRate, int riskScore,
                             String qualityGrade, String route, boolean publishAllowed,
                             List<String> controls) {}
}
