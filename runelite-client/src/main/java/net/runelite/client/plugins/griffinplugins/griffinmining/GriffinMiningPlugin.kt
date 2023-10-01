package net.runelite.client.plugins.griffinplugins.griffintrainer

import com.google.inject.Provides
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.griffinplugins.griffinmining.GriffinMiningOverlay
import net.runelite.client.plugins.griffinplugins.griffinmining.GriffinMiningScript
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = Griffin + GriffinMiningPlugin.CONFIG_GROUP, enabledByDefault = false)
class GriffinMiningPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Mining Trainer"
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: GriffinMiningOverlay

    @Inject
    private lateinit var config: GriffinTrainerConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): GriffinTrainerConfig {
        return configManager.getConfig(GriffinTrainerConfig::class.java)
    }

    @Inject
    lateinit var miningScript: GriffinMiningScript

    override fun startUp() {
        miningScript = GriffinMiningScript()

        overlayManager.add(overlay)
        miningScript.run(config)
    }

    override fun shutDown() {
        miningScript.shutdown()
        overlayManager.remove(overlay)
    }
}
