package org.example.odoru.export;

import org.example.odoru.entities.Role;

public record TokenExport(String token, Role role, Long membreId) {}