package com.mycity4kids.gtmutils;

import com.google.android.gms.tagmanager.ContainerHolder;

/**
 * Created by anshul on 2/10/16.
 */
public class ContainerHolderSingleton {
    private static ContainerHolder containerHolder;

    /**
     * Utility class; don't instantiate.
     */
    private ContainerHolderSingleton() {
    }

    public static ContainerHolder getContainerHolder() {
        return containerHolder;
    }

    public static void setContainerHolder(ContainerHolder c) {
        containerHolder = c;
    }
}
