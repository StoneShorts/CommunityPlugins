package com.example;

import com.communitydev.Riv.RivGoblins.RivGoblinsPlugin;
import com.communitydev.biggs.BiggFires.BiggFiresPlugin;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RivPluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,
        BiggFiresPlugin.class, RivGoblinsPlugin.class); //oh yeah, sec. when loading from IDE - you have to remove this from sideloaded. dw it doesnt affect current open clients. Just update again before next use of UZ (Using loader)
        RuneLite.main(args);
    }
}
