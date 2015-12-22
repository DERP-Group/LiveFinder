/**
 * Copyright (C) 2015 David Phillips
 * Copyright (C) 2015 Eric Olson
 * Copyright (C) 2015 Rusty Gerard
 * Copyright (C) 2015 Paul Winters
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.derpgroup.derpwizard.voice.model;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Factory for building messages.
 *
 * @author Rusty
 * @since 0.0.1
 * @see VoiceInput
 * @see VoiceOutput
 */
public class VoiceMessageFactory {
  public enum InterfaceType {
    ALEXA
  }

  private static final Map<InterfaceType, Class<?>> INPUT_MAP = new HashMap<InterfaceType, Class<?>>();

  static {
    INPUT_MAP.put(InterfaceType.ALEXA, AlexaInput.class);
  }

  private VoiceMessageFactory() {
    throw new AssertionError("Do not instantiate this class");
  }

  /**
   * Builds a wrapper around the user's voice request.
   *
   * @param request
   *          The request object, not null
   * @param type
   *          The voice interface type that sent the request, not null
   * @return A VoiceInput wrapper, never null
   */
  public static @NonNull VoiceInput buildInputMessage(@NonNull Object request, @NonNull InterfaceType type) {
    if (!INPUT_MAP.containsKey(type)) {
      throw new IllegalArgumentException("Invalid type: " + type);
    }

    try {
      return (VoiceInput) INPUT_MAP.get(type).getConstructor(Object.class).newInstance(request);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new UnsupportedOperationException("Failed to build instance", e);
    }
  }

  /**
   * Builds a wrapper around the user's voice request.
   *
   * @param request
   *          The request object, not null
   * @param metadata
   *          The metadata associated with the request, not null
   * @param type
   *          The voice interface type that sent the request, not null
   * @return A VoiceInput wrapper, never null
   */
  public static @NonNull VoiceInput buildInputMessage(@NonNull Object request, @NonNull CommonMetadata metadata, @NonNull InterfaceType type) {
    if (!INPUT_MAP.containsKey(type)) {
      throw new IllegalArgumentException("Invalid type: " + type);
    }

    try {
      return (VoiceInput) INPUT_MAP.get(type).getConstructor(Object.class, CommonMetadata.class).newInstance(request, metadata);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new UnsupportedOperationException("Failed to build instance", e);
    }
  }
}
