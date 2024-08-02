package org.an5w3r.an5w3rBot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomInfo {
        private String areaName;
        private String userCover;
        private String title;
        private boolean isLive;
        private String streamUrl;

    }