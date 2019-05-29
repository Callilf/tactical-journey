package com.dokkaebistudio.tacticaljourney.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;

/**
 * Represents the list of sentences a PNJ can say.
 * Each sentences can have conditions to appear.
 * @author Callil
 *
 */
public abstract class AbstractDialogs {

	/**
	 * Base dialogs.
	 */
	private List<Dialog> dialogs = new ArrayList<>();
	
	/**
	 * Dialogs that can be told only if a predicate is satisfied.
	 */
	private Map<Predicate<PublicEntity>, List<Dialog>> conditionalDialogs = new HashMap<>();
	
	/**
	 * Dialogs that can be told only when getDialog is called with a given tag.
	 */
	private Map<String, List<Dialog>> taggedDialogs = new HashMap<>();
	
	
	protected abstract void setSpeaker(Dialog d);
	
	public void addDialog(Dialog d) {
		
		if (d.getCondition() != null) {
			
			List<Dialog> condDialogs = conditionalDialogs.get(d.getCondition());
			if (condDialogs == null) {
				condDialogs = new ArrayList<>();
			}
			condDialogs.add(d);
			conditionalDialogs.put(d.getCondition(), condDialogs);
		
		} else {
			dialogs.add(d);
		}
	
	}
	
	public void addDialog(String tag, Dialog d) {
		List<Dialog> tagged = taggedDialogs.get(tag);
		if (tagged == null) {
			tagged = new ArrayList<>();
		}
		tagged.add(d);
		taggedDialogs.put(tag, tagged);
	}
	
	public Dialog getDialog(PublicEntity speakerEntity) {
		Dialog dialogToUse = null;
		
		// Check if one of the condition is satisfied
		Optional<Predicate<PublicEntity>> matchingKey = conditionalDialogs.keySet().parallelStream()
			.filter( p -> p.test(speakerEntity))
			.findFirst();
		
		if (matchingKey.isPresent()) {
			// A condition is satisfied
			List<Dialog> list = conditionalDialogs.get(matchingKey.get());
			dialogToUse = list.remove(0);
			
			if (dialogToUse.isRepeat()) {
				list.add(dialogToUse);
			} else {
				if (list.size() == 0) {
					conditionalDialogs.remove(matchingKey.get());
				}
			}
		} else {
			//No condition validated
			dialogToUse = dialogs.remove(0);
			
			if (dialogToUse.isRepeat()) {
				dialogs.add(dialogToUse);
			}
		}
		
		this.setSpeaker(dialogToUse);
		return dialogToUse;
	}
	
	public Dialog getDialog(String tag) {
		Dialog res = null;
		
		List<Dialog> list = taggedDialogs.get(tag);
		res = list.remove(0);
		
		if (res.isRepeat()) {
			list.add(res);
		} else {
			if (list.size() == 0) {
				taggedDialogs.remove(tag);
			}
		}
		
		this.setSpeaker(res);
		return res;
	}
}
