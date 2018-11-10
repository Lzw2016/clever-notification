package org.clever.notification.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;
import org.clever.common.validation.ValidIntegerStatus;

import javax.validation.constraints.Future;
import java.util.Date;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-10 17:56 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReceiverBlackListUpdateReq extends BaseRequest {

    @ApiModelProperty("是否启用，0：禁用；1：启用")
    @ValidIntegerStatus(value = {0, 1})
    private Integer enabled;

    @ApiModelProperty("黑名单帐号过期时间(到期自动禁用)")
    @Future
    private Date expiredTime;
}
