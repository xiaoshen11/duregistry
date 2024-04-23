package com.bruce.duregistry.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Registry server instance
 * @date 2024/4/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"url"})
public class Server {

    private String url;

    private boolean status;

    private boolean leader;

    private long version;

}
