package com.photoapp.api.albums.service;

import java.util.List;

import com.photoapp.api.albums.data.AlbumEntity;

public interface AlbumService {
	
	List<AlbumEntity> getAlbums(String userId);

}
