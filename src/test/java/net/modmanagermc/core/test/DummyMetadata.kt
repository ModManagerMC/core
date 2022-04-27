package net.modmanagermc.core.test

import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.metadata.*
import java.util.*

class DummyMetadata(private val modId: String, private val versionStr: String) : ModMetadata {
    override fun getType(): String {
        return "builtin"
    }

    override fun getId(): String {
        return modId
    }

    override fun getProvides(): MutableCollection<String> {
        return mutableListOf()
    }

    override fun getVersion(): Version {
        return Version.parse(versionStr)
    }

    override fun getEnvironment(): ModEnvironment {
        return ModEnvironment.CLIENT
    }

    override fun getDependencies(): MutableCollection<ModDependency> {
        return mutableListOf()
    }

    override fun getName(): String {
        return modId
    }

    override fun getDescription(): String {
        return ""
    }

    override fun getAuthors(): MutableCollection<Person> {
        return mutableListOf()
    }

    override fun getContributors(): MutableCollection<Person> {
        return mutableListOf()
    }

    override fun getContact(): ContactInformation? {
        return null
    }

    override fun getLicense(): MutableCollection<String> {
        return mutableListOf()
    }

    override fun getIconPath(size: Int): Optional<String> {
        return Optional.empty()
    }

    override fun containsCustomValue(key: String?): Boolean {
        return false
    }

    override fun getCustomValue(key: String?): CustomValue? {
        return null
    }

    override fun getCustomValues(): MutableMap<String, CustomValue> {
        return mutableMapOf()
    }

    @Deprecated("Deprecated in Java", ReplaceWith(""))
    override fun containsCustomElement(key: String?): Boolean {
        return false
    }

}
