package bpm.architect.web.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.SuggestOracle.Response;

public class CustomMultiWordSuggest extends MultiWordSuggestOracle{

	private Response defaultResponse = new Response(new ArrayList<Suggestion>());
	
	public CustomMultiWordSuggest(){
		super();
	}
	
	@Override
	public void requestDefaultSuggestions(Request request, Callback callback) {
//		if (defaultResponse != null) {
//			callback.onSuggestionsReady(request, defaultResponse);
//	    } else {
	    	super.requestDefaultSuggestions(request, callback);
//	    }
	}

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		if (defaultResponse != null) {
			int limit = request.getLimit();

		    // Respect limit for number of choices.
		    int numberTruncated = Math.max(0, defaultResponse.getSuggestions().size() - limit);
		    List<MultiWordSuggestion> f = (List<MultiWordSuggestion>) defaultResponse.getSuggestions();
		    for (int i = f.size() - 1; i > limit; i--) {
		    	f.remove(i);
		    }

		    Response response = new Response(f);
		    response.setMoreSuggestionsCount(numberTruncated);

		    callback.onSuggestionsReady(request, response);
			//callback.onSuggestionsReady(request, defaultResponse);
	    } else {
	    	super.requestDefaultSuggestions(request, callback);
	    }
	}
	
	@Override
	public void add(String candidate) {
		List<MultiWordSuggestion> suggestions = (List<MultiWordSuggestion>) defaultResponse.getSuggestions();
		SafeHtmlBuilder accum = new SafeHtmlBuilder();
		accum.appendEscaped(candidate);
		MultiWordSuggestion suggestion = createSuggestion(candidate, accum.toSafeHtml().asString());
		suggestions.add(suggestion);
		defaultResponse = new Response(suggestions);
		super.add(candidate);
	}

	public void addAllFiltered(Collection<String> collection) {
		SafeHtmlBuilder accum = new SafeHtmlBuilder();
		List<MultiWordSuggestion> suggestions = new ArrayList<MultiWordSuggestion>();
		for(String candidate : collection){
			accum.appendEscaped(candidate);
			MultiWordSuggestion suggestion = createSuggestion(candidate, accum.toSafeHtml().asString());
			suggestions.add(suggestion);
		}
		 
		defaultResponse = new Response(suggestions);
		super.addAll(collection);
	}
	
	public void clear() {
		defaultResponse = new Response(new ArrayList<Suggestion>());
	    super.clear();
	  }
}
