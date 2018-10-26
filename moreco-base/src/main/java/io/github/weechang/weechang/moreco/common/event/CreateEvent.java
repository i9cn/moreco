package io.github.weechang.weechang.moreco.common.event;


import io.github.weechang.weechang.moreco.query.domain.AuditEntry;

/**
 * 说明：
 *
 * @author zhangwei
 * @version 2017/11/20 13:27.
 */
public class CreateEvent extends AuditableAbstractEvent {

    public CreateEvent(String id, AuditEntry auditEntry) {
        super(id, auditEntry);
    }
}
