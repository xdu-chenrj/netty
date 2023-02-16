package org.entity;


import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RpcRequest {
    private String interfaceName;
    private String methodName;
}
