package ru.mvrlrd.chat_settings.domain

interface SettingsRepository {
    suspend fun saveSettings(chatSettings: ChatSettings)
}